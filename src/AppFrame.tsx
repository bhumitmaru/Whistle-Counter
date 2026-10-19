import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import AppRoutes from "./AppRoutes";

export default function AppFrame() {
  return <TooltipProvider><Toaster /><Sonner /><AppRoutes /></TooltipProvider>;
}
