import "./App.css";
import FoodOasisMap from "./components/FoodOasisMap";

function App() {
  const startLocation = {
    address: "1600 Amphitheatre Parkway, Mountain View, california.",
    lat: 39.742043,
    lng: -104.991531,
  };
  return (
    <div className="App">
      <h1>FOOD OASIS</h1>
      <FoodOasisMap startLocation={startLocation} />
    </div>
  );
}

export default App;
