import AppHeader from "../components/AppHeader.jsx";
import {Outlet} from "react-router-dom";

function RootLayout() {
    return (
        <>
            <AppHeader />
            <Outlet />
        </>
    )
}

export default RootLayout;