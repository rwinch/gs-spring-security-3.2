var webpage = require('webpage'),
		page = webpage.create(),
		fs = require('fs');

page.open('temp-output.html', function(status) {
	page.viewportSize = {
		width: 1120,
		height: 768
	};
	page.paperSize = {
		width: 1680,
		height: 1152
	};
	page.render('output.pdf');

	fs.removeTree('temp-slides');
	fs.remove('temp-output.html');
	phantom.exit();
});