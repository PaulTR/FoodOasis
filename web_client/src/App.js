import "./App.css";
import FoodOasisMap from "./components/FoodOasisMap";

function App() {
  const startLocation = {
    address: "DENVER",
    lat: 39.742043,
    lng: -104.991531,
  };
  const googleMapsAPIKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
  return (
    <div className="App">
      <h1>FOOD OASIS</h1>
      <FoodOasisMap
        location={startLocation}
        zoomLevel={12}
        googleMapsAPIKey={googleMapsAPIKey}
      />
    </div>
  );
}

export default App;
