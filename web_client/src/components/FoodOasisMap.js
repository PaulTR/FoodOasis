import React from "react";
import GoogleMapReact from "google-map-react";
import "./style/foodOasisMap.css";

const _onClick = ({ lat, lng }) =>
  console.log("latitude of click: ", lat, "longitude of click: ", lng);

export const FoodOasisMap = ({ location, zoomLevel, googleMapsAPIKey }) => (
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
export default FoodOasisMap;
