import React, { useState, useEffect } from "react";
import GoogleMapReact from "google-map-react";
import axios from "axios";
import "./style/foodOasisMap.css";

export const FoodOasisMap = ({ location, zoomLevel, googleMapsAPIKey }) => {
  let [lastCoordinates, setLastCoordinates] = useState({
    mostRecentUserLAT: 0,
    mostRecentUserLNG: 0,
  });
  //function handler for user clicking on the map, gets mapped to GoogleMapReact component's "onClick" event below
  const _onClick = ({ lat, lng }) => {
    setLastCoordinates({ mostRecentUserLAT: lat, mostRecentUserLNG: lng });
  };
  //hook runs when user updates last coordinates
  useEffect(() => {
    console.log(
      "latitude of click: ",
      lastCoordinates.mostRecentUserLAT,
      "longitude of click: ",
      lastCoordinates.mostRecentUserLNG
    );
    axios({
      method: "post",
      url:
        "https://us-central1-food-oasis-f4d73.cloudfunctions.net/getPointScore",
      headers: { "content-type": "application/json" },
      data: {
        lat: lastCoordinates.mostRecentUserLAT,
        lng: lastCoordinates.mostRecentUserLNG,
      },
    })
      .then((res) => {
        console.log(res);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [lastCoordinates]);
  return (
    <div className="map">
      <h2 className="map-h2">Click on the map to see FoodOasis score!</h2>

      <div className="google-map">
        <GoogleMapReact
          bootstrapURLKeys={{ key: googleMapsAPIKey }}
          defaultCenter={location}
          defaultZoom={zoomLevel}
          onClick={_onClick}
        />
      </div>
    </div>
  );
};
export default FoodOasisMap;
