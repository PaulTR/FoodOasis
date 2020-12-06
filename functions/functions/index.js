const functions = require('firebase-functions');
const interwebs_request = require('request');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.helloWorld = functions.https.onRequest((request, response) => 
{
	return response.status(200).json({data: { "test": 123 }});
});

exports.getLocationInfo = functions.https.onRequest( async (request, response) => 
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

	var zipData = null;
	await admin.database().ref('zip/' + zip).once('value', async (snapshot) => {
		zipData = snapshot.val();
		await admin.database().ref('counties/' + snapshot.val().county).once('value', async (snapshot) => {
			var query = admin.firestore().collection('bus_stop')
				.where('zip', '==', zip);

			var closest_stop = null;
			var tmp = null;
			var cur_distance = 999999;
			var distance = 0;
			query.get().then(async querySnapshot => {
				await querySnapshot.forEach(documentSnapshot => {
					tmp = documentSnapshot.data();
					distance = Math.sqrt((Math.pow(tmp.lat - lat, 2)+Math.pow(tmp.lng-lng, 2)));
					if( distance < cur_distance ) {
						closest_stop = tmp;
						cur_distance = distance;
					}
					console.log(documentSnapshot.data());
				});
				return response.status(200).json({data: {closest_stop_distance_miles: cur_distance*69, zipcode: zip, countyInfo: snapshot.val(), zipInfo: zipData, bus_stop: closest_stop}});
			});
		});
	});
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

	var zipData = null;
	await admin.database().ref('zip/' + zip).once('value', async (snapshot) => {
		zipData = snapshot.val();
		await admin.database().ref('counties/' + snapshot.val().county).once('value', async (snapshot) => {
			var query = admin.firestore().collection('bus_stop')
				.where('zip', '==', zip);

			var closest_stop = null;
			var tmp = null;
			var cur_distance = 999999;
			var distance = 0;
			query.get().then(async querySnapshot => {
				await querySnapshot.forEach(documentSnapshot => {
					tmp = documentSnapshot.data();
					distance = Math.sqrt((Math.pow(tmp.lat - lat, 2)+Math.pow(tmp.lng-lng, 2)));
					if( distance < cur_distance ) {
						closest_stop = tmp;
						cur_distance = distance;
					}
					console.log(documentSnapshot.data());
				});
				return response.status(200).json({data: {closest_stop_distance_miles: cur_distance*69, zipcode: zip, countyInfo: snapshot.val(), zipInfo: zipData, bus_stop: closest_stop}});
			});
		});
	});
});