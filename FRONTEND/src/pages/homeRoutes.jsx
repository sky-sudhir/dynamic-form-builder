import React, { lazy } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import { Suspense } from "../components/ui/suspense";
import Topbar from "../components/home/Topbar";
import NavigationBar from "../components/home/NavigationBar";
import ProtectedRoute from "../components/auth/ProtectedRoute";

const Dashboard = lazy(() => import("../components/home/dashboard/dashboard"));
const Reports = lazy(() => import("../components/home/reports/Reports"));
const Analytics = lazy(() => import("../components/home/analytics/Analytics"));
const Settings = lazy(() => import("../components/home/settings/Settings"));
const FormRoutes = lazy(() => import("./formRoutes/FormRoutes"));

function HomeRoutes() {
  return (
    <ProtectedRoute>
      <Suspense>
        <div className="min-h-screen bg-background">
          <NavigationBar />
          <div className="lg:pl-72">
            <Topbar />
            <main className="container mx-auto py-6 px-4 lg:px-6">
              <Routes>
                <Route path="" element={<Dashboard />} />
                <Route path="form/*" element={<FormRoutes />} />
                <Route path="reports" element={<Reports />} />
                <Route path="analytics" element={<Analytics />} />
                <Route path="settings" element={<Settings />} />
                <Route path="*" element={<Navigate replace to="/404" />} />
              </Routes>
            </main>
          </div>
        </div>
      </Suspense>
    </ProtectedRoute>
  );
}

export default HomeRoutes;
