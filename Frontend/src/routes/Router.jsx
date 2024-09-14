import { createBrowserRouter } from "react-router-dom";
import Layout from "./Layout";
import ProtectedRoute from "./ProtectedRoute";

import Login from "../Auth/Login";
import Home from "../pages/Home";

import About from "../pages/About";
import NotFound from "../pages/NotFound";
import Profile from "../pages/Profile";
import PetDetail2 from "../pages/PetDetail2";
import EditarPet from "../pages/EditarPet";
import ConsultationDetail from "../pages/ConsultationDetail";

const Router = createBrowserRouter(
    [
        {
            element: <Layout />,
            children: [
                {
                    path: "/",
                    element: <Login />,
                },
                {
                    path: "/home",
                    element:
                        (
                            <ProtectedRoute>
                                <Home />
                            </ProtectedRoute>
                        ),
                },
                {
                    path: "/profile",
                    element: (
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    ),
                },
                {
                    path: "/mascota",
                    children: [
                        {
                            path: ":id",
                            element: (
                                <ProtectedRoute>
                                    <PetDetail2 />
                                </ProtectedRoute>
                            ),
                        },
                    ]

                },
                {
                    path: "/mascota/editar",
                    children: [
                        {
                            path: ":id",
                            element: (                             
                                    <EditarPet />                            
                            ),
                        },
                    ]

                },
                {
                    path: "/mascota/consulta",
                    children: [
                        {
                            path: ":id",
                            element: (                             
                                    <ConsultationDetail />                            
                            ),
                        },
                    ]
                },


                
                // {
                //     path: "/login",
                //     element: <Login />,
                // },            

            ],
        },
    ],
    // {
    //     basename: "/....",
    // }
);

export default Router;