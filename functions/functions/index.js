const functions = require('firebase-functions');
//const interwebs_request = require('request');

// exports.helloWorld = functions.https.onRequest((request, response) => 
exports.helloWorld = functions.https.onRequest((request, response) => 
{
 //var inputtext = request.body.data.text;
 // var inputtext = data.text;
 
 // console.log(inputtext);

 //required json object here
 //response.status(200).send("{ data: { \"message\": \""+inputtext+"\"}}");
 return response.status(200).json({data: { "test": 123 }});
 //return "{ \"message\": \""+inputtext+"\"}";
});

// exports.getPointScore = functions.https.onCall((data, context) => 
// {
// 	var lat = data['lat'];
// 	var lng = data['lng'];

// 	var options = {
// 		url: "http://open.mapquestapi.com/geocoding/v1/reverse?key=qzttnJ2q62vLFGf8spd0LJDkY3ksgwa9&location="+lat+","+lng,
// 		json: true
// 	};

// 	return new Promise(function (resolve, reject) {
// 		request(options, function(err, resp) {
// 			return resolve({text:resp.body.value.results[0].locations.postalCode})
// 		});
// 	})
// });