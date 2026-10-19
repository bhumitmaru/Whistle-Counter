import { BrowserRouter } from "react-router-dom";
import AppFrame from "./AppFrame";

const App = () => (
  <BrowserRouter basename={import.meta.env.BASE_URL}><AppFrame /></BrowserRouter>
);

export default App;
