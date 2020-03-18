import React, { Component } from "react"
import { Text } from 'react-native';
import InstallReferralModule from './InstallReferralModule';
class CustomComponent extends Component {
    render() {
        return <Text>Harsh</Text>
    }

    componentDidMount() {
        InstallReferralModule.getReferral().then ((value) => {
            console.log(value);
        })
    }
}

export default CustomComponent;