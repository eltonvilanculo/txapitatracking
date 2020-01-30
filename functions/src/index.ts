import * as functions from 'firebase-functions';

export const helloWorld = functions.https.onRequest((req,data)=>{
 console.log("Hello world");
}); 
