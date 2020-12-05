import React from "react";
import GoogleMapReact from "google-map-react";
import "./style/foodOasisMap.css";

const googleMapsAPIKey = process.env.GOOGLE_MAPS_API_KEY;
export const FoodOasisMap = ({ location, zoomLevel }) => (
  <div className="map">
    <h2 className="map-h2">Click on the map to see FoodOasis score!</h2>

    <div className="google-map">
      <GoogleMapReact
        bootstrapURLKeys={{ key: googleMapsAPIKey }}
        defaultCenter={location}
        defaultZoom={zoomLevel}
      >
        {/* <LocationPin
          lat={location.lat}
          lng={location.lng}
          text={location.address}
        /> */}
      </GoogleMapReact>
    </div>
  </div>
);
export default FoodOasisMap;
