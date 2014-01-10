/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static samples.test.SecurityRequestPostProcessors.csrf;
import static samples.test.SecurityRequestPostProcessors.user;

import java.util.List;

import javax.servlet.Filter;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import sample.config.RootConfiguration;
import sample.data.Message;
import sample.data.User;
import sample.mvc.config.WebMvcConfiguration;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfiguration.class,
		WebMvcConfiguration.class })
@WebAppConfiguration
public class SecurityTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Filter springSecurityFilterChain;

	private User rob;

	private MockMvc mvc;

	@Before
	public void setup() {
		// NOTE: Could also load rob from UserRepository if we wanted
		rob = new User();
		rob.setId(0L);
		rob.setEmail("rob@example.com");
		rob.setFirstName("Rob");
		rob.setLastName("Winch");

		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilters(springSecurityFilterChain)
				.build();
	}

	@Test
	public void inboxRequiresLogin() throws Exception {
		mvc
			.perform(get("/"))
			.andExpect(loginPage());
	}

	@Test
	public void inboxShowsOnlyRobsMessages() throws Exception {
		RequestBuilder request = get("/")
				.with(user(rob).roles("USER"));

		mvc
			.perform(request)
			.andExpect(model().attribute("messages", new BaseMatcher<List<Message>>() {
				@Override
				public boolean matches(Object other) {
					@SuppressWarnings("unchecked")
					List<Message> messages = (List<Message>) other;
					return messages.size() == 1 && messages.get(0).getId() == 100;
				}

				@Override
				public void describeTo(Description d) {
				}
			}));
	}

	@Test
	public void invalidUsernamePassword() throws Exception {
		RequestBuilder request = post("/login")
			.param("username", "rob@example.com")
			.param("password", "invalid")
			.with(csrf());

		mvc
			.perform(request)
			.andExpect(invalidLogin());
	}

	@Test
	public void validUsernamePassword() throws Exception {
		RequestBuilder request = post("/login")
			.param("username", "rob@example.com")
			.param("password", "password")
			.with(csrf());

		mvc
			.perform(request)
			.andExpect(redirectedUrl("/"));
	}

	@Test
	public void composeRequiresCsrf() throws Exception {
		RequestBuilder request = post("/")
					.with(user(rob).roles("USER"));

		mvc
			.perform(request)
			.andExpect(invalidCsrf());
	}

	@Test
	public void robCannotAccessLukesMessage() throws Exception {
		RequestBuilder request = get("/110")
					.with(user(rob).roles("USER"));

		mvc
			.perform(request)
			.andExpect(status().isForbidden());
	}

	@Test
	public void robCanAccessOwnMessage() throws Exception {
		RequestBuilder request = get("/100")
					.with(user(rob).roles("USER"));

		mvc
			.perform(request)
			.andExpect(status().isOk());
	}

	private static ResultMatcher loginPage() {
		return new ResultMatcher() {
			@Override
			public void match(MvcResult result) throws Exception {
				status().isMovedTemporarily().match(result);
				redirectedUrl("http://localhost/login").match(result);
			}
		};
	}

	private static ResultMatcher invalidLogin() {
		return new ResultMatcher() {
			@Override
			public void match(MvcResult result) throws Exception {
				status().isMovedTemporarily().match(result);
				redirectedUrl("/login?error").match(result);
			}
		};
	}

	private static ResultMatcher invalidCsrf() {
		return new ResultMatcher() {
			@Override
			public void match(MvcResult result) throws Exception {
				status().isForbidden().match(result);
			}
		};
	}
}
