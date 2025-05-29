import classes from './AppHeader.module.css'
import {useState} from "react";

function AppHeader() {
    const [dropDownVisible, setDropDownVisible] = useState(false);

    function showDropDown() {
        setDropDownVisible(true);
    }

    function hideDropDown() {
        setDropDownVisible(false);
    }

    return (
        <header className={classes.header}>
            <div className={classes.logo}>
                <img src="/assets/images/spotify-logo.png" width="50" height="50" alt="spotify-logo"/>
            </div>
            <div className={classes.profile_container} onClick={showDropDown} onBlur={hideDropDown}>
                <div className={classes.profile_hover_area}>
                    <img src="/assets/images/profile-icon.png" alt="User Profile" className={classes.profile_icon}/>
                </div>
                {dropDownVisible && (
                    <div className={classes.dropdown_menu}>
                        <button className={classes.dropdown_item}>Logout</button>
                    </div>
                )}
            </div>
        </header>
    );
}

export default AppHeader;