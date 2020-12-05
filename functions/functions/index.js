const functions = require('firebase-functions');

// exports.helloWorld = functions.https.onRequest((request, response) => 
exports.helloWorld = functions.https.onCall((data, context) => 
{
 //var inputtext = request.body.data.text;
 var inputtext = data.text;
 
 console.log(inputtext);

 //required json object here
 // response.status(200).send("{ data: { \"message\": \""+inputtext+"\"}}");
 return "{ \"message\": \""+inputtext+"\"}";
});

exports.getPointScore = functions.https.onCall((data, context) => 
{
	var lat = data['lat'];
	var lng = data['lng'];

	var product = lat * lng;

	return "{ \"score\": \""+ product +"\"}";
});