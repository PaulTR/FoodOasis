import "./App.css";
import FoodOasisMap from "./components/FoodOasisMap";

function App() {
  const startLocation = {
    address: "DENVER",
    lat: 39.742043,
    lng: -104.991531,
  };
  return (
    <div className="App">
      <h1>FOOD OASIS</h1>
      <FoodOasisMap location={startLocation} zoomLevel={7} />
    </div>
  );
}

export default App;
