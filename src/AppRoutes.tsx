import { Route, Routes } from "react-router-dom";
import SiteLayout from "./components/SiteLayout";
import Index from "./pages/Index";
import HowItWorks from "./pages/HowItWorks";
import About from "./pages/About";
import FAQ from "./pages/FAQ";
import NotFound from "./pages/NotFound";

export default function AppRoutes() {
  return <Routes><Route element={<SiteLayout />}><Route path="/" element={<Index />} /><Route path="/how-it-works" element={<HowItWorks />} /><Route path="/about" element={<About />} /><Route path="/faq" element={<FAQ />} /><Route path="*" element={<NotFound />} /></Route></Routes>;
}
