import Swagger2Client from '../service/Swagger2Client.js'
import * as observable from '../service/EventObservable.js'

let appUrl = 'http://localhost:8888';
let ajaxOpts = {
			crossDomain: true,
			beforeSend: function (xhr) {
				xhr.setRequestHeader("Authorization", "Basic " + window.btoa('rajeevn:root'));
			}
		};
if (process.env.NODE_ENV === 'production') {
	appUrl = '';
	ajaxOpts = {};
}
window.$R = Swagger2Client(appUrl + '/v2/api-docs', ajaxOpts, appUrl, true, true);

window.$E = observable;

