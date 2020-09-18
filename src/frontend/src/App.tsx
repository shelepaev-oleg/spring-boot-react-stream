import * as React from 'react';
import '@scss/main.scss';
import '@css/antd.css';
import {observer} from "mobx-react";
import UploadBigFile from "./components/UploadBigFile";

interface IProps {
}

@observer
class App extends React.Component<IProps> {

    constructor(props: IProps) {
        super(props);
    }

    render() {
        return <React.Fragment>
            <UploadBigFile/>
        </React.Fragment>
    }
}

export default App;
