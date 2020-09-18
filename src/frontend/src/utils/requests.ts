import axios from 'axios';
import qs from 'qs';
import {IRequests} from "../interface/IRequests";
import streamSaver from 'streamsaver';
import { WritableStream } from 'web-streams-polyfill/ponyfill';

/**
 * запросы для api backend
 */
const backendApiRequests: IRequests = {
    uri(uri: string) {
        return `/api/rest/${uri}`;
    },
    delete(path: string, data: any, config: any) {
        const configWithData = {
            ...config,
            data,
        };
        return axios.delete(this.uri(path), configWithData);
    },
    get(path: string, data: any, config: any) {
        return axios.get(this.uri(path), {
            ...config,
            params: data,
            paramsSerializer: (params: any) => qs.stringify(params),
        });
    },
    post(path: string, data: any, config: any) {
        return axios.post(this.uri(path), data, config);
    },
    put(path: string, data: any, config: any) {
        return axios.put(this.uri(path), data, config);
    },
    // https://github.com/axios/axios
    // https://futurestud.io/tutorials/download-files-images-with-axios-in-node-js
    getStream(path: string) {
        // Работает, но не показывает прогресс
        // AxiosStream.download("apache-maven-3.6.3-bin", 'zip', {
        //     method: 'get',
        //     url: this.uri(path),
        // } as AxiosRequestConfig);

        // Скачивает не дождавшись окончания стрима
        // https://www.onooks.com/how-to-consume-the-download-stream-from-axios-using-streamsaver-js/
        // axios({
        //     method: 'get',
        //     url: this.uri(path),
        //     responseType: 'stream'
        // }).then(response => {
        //     const fileStream = streamSaver.createWriteStream('disableUdp.rar');
        //     // new Response(response.data).body
        //     //     .pipeTo(fileStream);
        //     response.data.pipe(fileStream);
        // })
        // .catch(error => {
        //     console.log(error);
        // });

        // Работает!
        // https://stackoverflow.com/questions/58609130/how-to-consume-the-download-stream-from-axios-using-streamsaver-js
        fetch(this.uri(path), {
            method: 'GET'
        }).then(response => {

            let contentDisposition = response.headers.get('Content-Disposition');
            let fileName = contentDisposition.substring(contentDisposition.lastIndexOf('=') + 1);

            // These code section is adapted from an example of the StreamSaver.js
            // https://jimmywarting.github.io/StreamSaver.js/examples/fetch.html

            // If the WritableStream is not available (Firefox, Safari), take it from the ponyfill
            if (!window.WritableStream) {
                streamSaver.WritableStream = WritableStream;
                window.WritableStream = WritableStream;
            }

            const fileStream = streamSaver.createWriteStream(fileName);
            const readableStream = response.body;

            // More optimized
            if (readableStream.pipeTo) {
                return readableStream.pipeTo(fileStream);
            }

            const writer: any = fileStream.getWriter();

            const reader = response.body.getReader();
            const pump = () => reader.read()
                .then(res => res.done
                    ? writer.close()
                    : writer.write(res.value).then(pump));

            pump();
        })
        .catch(error => {
            console.log(error);
        });


        // Не удалось подключить модуль fs
        // axios.get(this.uri(path), {
        //     responseType: 'stream',
        // }).then(res => {
        //     fileDownload(res.data, fileName);
        // });
        // axios({
        //     method: 'get',
        //     url: this.uri(path),
        //     responseType: 'stream'
        // })
        // .then(function (response) {
            // const fs = window.require('fs');
            // const fs = require('fs');

            // https://github.com/jprichardson/node-fs-extra
            // const fse = require('fs-extra');
            // response.data.pipe(fse.createWriteStream(__dirname));

            // fs.writeFileSync('./south-beach.jpg', response.data);
            // response.data.pipe(fs.createWriteStream(__dirname));
            // response.data.pipe(fs.createWriteStream(__dirname))
        // });
    }
};

/**
 * все api
 */
const requests = {
    backendApi: backendApiRequests
};

export default requests;
