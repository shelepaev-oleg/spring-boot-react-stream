import requests from "../utils/requests";
import {AxiosPromise} from "axios";

const config = { withCredentials: true };

/**
 * Загрузка большого файла
 * @param formData
 */
export const uploadLargeFile = (formData: FormData): AxiosPromise<void> =>
    requests.backendApi.post(`upload-multi-part-file`, formData, config);

/**
 * Выгрузка большого файла
 * @param fileName
 */
export const downloadLargeFile = (testFileId: number): void =>
    requests.backendApi.getStream(`download/${testFileId}`);
