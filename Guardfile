require 'asciidoctor'
require 'erb'
require 'haml'
require 'tilt'

guard 'shell' do
  watch(/^.*\.adoc$/) {|m|
    Asciidoctor.render_file(m[0], :to_dir => "./slides", :safe => Asciidoctor::SafeMode::UNSAFE, :template_dir => "../asciidoctor-backends/haml")
  }
end

guard 'livereload' do
  watch(%r{.+\.(css|js|html)$})
end
