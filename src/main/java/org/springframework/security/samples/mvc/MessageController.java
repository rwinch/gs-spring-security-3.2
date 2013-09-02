/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.security.samples.mvc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.samples.data.Message;
import org.springframework.security.samples.data.MessageRepository;
import org.springframework.security.samples.data.User;
import org.springframework.security.samples.data.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing {@link Message} instances.
 *
 * @author Rob Winch
 *
 */
@Controller
@RequestMapping("/")
public class MessageController {
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository,UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView list() {
        Iterable<Message> messages = messageRepository.findAll();
        return new ModelAndView("messages/inbox", "messages", messages);
    }

    @RequestMapping(value = "{id}", method=RequestMethod.GET)
    public ModelAndView view(@PathVariable Long id) {
        Message message = messageRepository.findOne(id);
        return new ModelAndView("messages/show", "message", message);
    }

    @RequestMapping(value = "{id}", method=RequestMethod.DELETE)
    public String delete(@PathVariable("id") Message message, RedirectAttributes redirect) {
        messageRepository.delete(message);
        redirect.addFlashAttribute("globalMessage", "Message removed successfully");
        return "redirect:/";
    }

    @RequestMapping(params="form", method=RequestMethod.GET)
    public String createForm(@ModelAttribute MessageForm messageForm) {
        return "messages/compose";
    }

    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView create(@Valid MessageForm messageForm, BindingResult result, RedirectAttributes redirect) {
        User to = userRepository.findByEmail(messageForm.getToEmail());
        if(to == null) {
            result.rejectValue("toEmail","toEmail", "User not found");
        }
        if(result.hasErrors()) {
            return new ModelAndView("messages/compose");
        }

        Message message = new Message();
        message.setSummary(messageForm.getSummary());
        message.setText(messageForm.getText());
        message.setTo(to);

        message = messageRepository.save(message);
        redirect.addFlashAttribute("globalMessage", "Message added successfully");
        return new ModelAndView("redirect:/{message.id}", "message.id", message.getId());
    }
}
