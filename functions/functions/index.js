const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.helloWorld = functions.https.onRequest((request, response) => 
{
 var inputtext = event.data.child('text').val();
 var d = new Date();
 var reply = "{\"data\": {\"message\": " + inputtext +", \"timestamp\": \""+d.getMilliseconds()+"\"}}";
 console.log(reply)
 response.send(reply);
});

