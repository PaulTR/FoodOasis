const functions = require('firebase-functions');
const interwebs_request = require('request');

// exports.helloWorld = functions.https.onRequest((request, response) => 
exports.helloWorld = functions.https.onRequest((request, response) => 
{
 //var inputtext = request.body.data.text;
 
 return response.status(200).json({data: { "test": 123 }});
});

exports.getPointScore = functions.https.onRequest( async (request, response) => 
{
	var lat = request.body.data['lat'];
	var lng = request.body.data['lng'];

	var options = {
		url: "http://open.mapquestapi.com/geocoding/v1/reverse?key=qzttnJ2q62vLFGf8spd0LJDkY3ksgwa9&location="+lat+","+lng,
		json: true
	};

	var zip = await new Promise(function (resolve, reject) {
		interwebs_request(options, function(err, resp) {
			return resolve(resp.body.results[0].locations[0].postalCode);
		});
	});

	return response.status(200).json({data: { "score": zip }});
});