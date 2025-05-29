import classes from './AppHeader.module.css'
import {useEffect, useRef, useState} from "react";
import {Link} from "react-router-dom";

function AppHeader() {
    const [dropDownVisible, setDropDownVisible] = useState(false);
    const dropDownRef = useRef(null);

    function showDropDown() {
        setDropDownVisible(true);
    }

    function hideDropDown() {
        setDropDownVisible(false);
    }

    useEffect(() => {
        function handleClickOutsideDropDown(event) {
            if (dropDownRef.current && !dropDownRef.current.contains(event.target)) {
                hideDropDown();
            }
        }

        if (dropDownVisible) {
            document.addEventListener('mousedown', handleClickOutsideDropDown);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutsideDropDown);
        }
    }, [dropDownVisible]);

    return (
        <header className={classes.header}>
            <div className={classes.logo}>
                <img src="/assets/images/spotify-logo.png" width="50" height="50" alt="spotify-logo"/>
            </div>
            <div className={classes.profile_container} ref={dropDownRef} onClick={showDropDown}>
                <div className={classes.profile_hover_area}>
                    <img src="/assets/images/profile-icon.png" alt="User Profile" className={classes.profile_icon}/>
                </div>
                {dropDownVisible && (
                    <div className={classes.dropdown_menu}>
                        <Link to='/auth' className={classes.dropdown_item}>Logout</Link>
                    </div>
                )}
            </div>
        </header>
    );
}

export default AppHeader;