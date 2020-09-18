import * as React from "react";
import {Upload} from "antd";
import Button from "antd/es/button";
import {UploadChangeParam, UploadFile} from "antd/es/upload/interface";
import {action, observable} from "mobx";
import {downloadLargeFile, uploadLargeFile} from "../api/rest";

interface IProps {
}

/**
 * Загрузка больших файлов
 */
class UploadBigFile extends React.Component<IProps> {

    /**
     * Выбранные файлы
     */
    @observable fileList: UploadFile[] = [];

    /**
     * Загружаемый файл
     */
    @observable file: File;

    /**
     * Загрузка файла
     */
    uploadFile() {
        const formData: FormData = new FormData();
        formData.append('file', this.file);
        uploadLargeFile(formData);
    }

    //https://github.com/jimmywarting/StreamSaver.js
    /**
     * Выгрузка файла
     */
    downloadFile() {
        const testFileId: number = 1;
        downloadLargeFile(testFileId);
    }

    /**
     * Обработчик выбора файла
     * @param info
     */
    @action
    handleUpload = async (info: UploadChangeParam): Promise<void> => {
        if (this.fileList.length > 0) {
            this.fileList = [];
        }
        let fileList = [...info.fileList];
        fileList = fileList.slice(-1);
        Promise.all(
            fileList.map((item: any) => item.originFileObj)
        ).then(attachments => {
            if (attachments && attachments.length > 0) {
                this.file = attachments[0];
            }
        });
        this.fileList = fileList;
    };

    render() {
        return <>
            <Upload
                fileList={this.fileList}
                multiple={false}
                onChange={this.handleUpload}
                beforeUpload={() => false}>
                <Button icon="upload">Выберите файл...</Button>
            </Upload>
            <Button htmlType="button" onClick={() => this.uploadFile()}>Загрузить файл</Button>
            <br/>
            <Button htmlType="button" onClick={() => this.downloadFile()}>Скачать файл</Button>
        </>
    }
}
export default UploadBigFile;
