let requiresNotNull = (data, key) => {
  if (data == null)
    throw Error(`${key} cannot be null`);
}

let verifyDataType = (data, type, key) => {
  if (type === 'array') {
    if (Object.prototype.toString.call(data) !== '[object Array]') {
      throw Error(`Invalid type for ${key}. Expecting ${type}.`);
    }
  }
  else if (typeof data !== type) {
    throw Error(`Invalid type for ${key}. Expecting ${type}.`);
  }
}

let camelize = (str) => {
	str = str.charAt(0).toLowerCase() + str.slice(1);
	return str.trim().replace(/[\s-][a-z]/g, function(match) {
		return match.slice(-1).toUpperCase();
	});
}

const events = {};
let componentRef = [];

class FireEvent {
	constructor(eventName) {
		this.handler = {};
		this.eventName = eventName;
	}
	
	fire() {
		for (let priority in this.handler) {
			for (let component of this.handler[priority]) {
				component[this.eventName](...arguments);
			}
		}
		return events;
	}

	fireAsync() {
		setTimeout(this.fire.bind(this), 0, ...arguments);
		return events;
	}
}

const subscribeFor = (event, component, priority='z') => {
	requiresNotNull(event, 'event');
	verifyDataType(event, 'string');
	event = camelize(event);
	requiresNotNull(component, 'component');
	verifyDataType(component, 'object');
	requiresNotNull(component[event], `component.${event}`);
	verifyDataType(component[event], 'function');
	if (!events[event]) {
		events[event] = new FireEvent(event);
	}
	if (!events[event].handler[priority]) {
		events[event].handler[priority] = [];
	}
	events[event].handler[priority].push(component);
	componentRef.push({
			'component': component, 
			'ref': events[event].handler[priority], 
			'i': events[event].handler[priority].length-1
		});
}

const subscribe = (component) => {
	for (let event of component.events()) {
		let eventName, priority;
		if (typeof event === 'string') {
			eventName = event;
			priority = 'z';
		}
		else {
			eventName = event.event;
			priority = event.priority;
		}
		subscribeFor(eventName, component, priority);
	}
}

const unsubscribe = (component) => {
	for (let i = 0; i < componentRef.length; i++) {
		let compRef = componentRef[i];
		if (compRef.component === component) {
			compRef.ref.splice(compRef.i, 1);
			componentRef.splice(i, 1);
			i--;
		}
	}
}

const fire = (event, ...args) => {
	let eventName = camelize(event);
	if (events[eventName]) {
		events[eventName].fire.apply(events[eventName], args);
	}
}

const fireAsync = (event, ...args) => {
	let eventName = camelize(event);
	if (events[eventName]) {
		events[eventName].fireAsync.apply(events[eventName], args);
	}
}

export {
	subscribe,
	subscribeFor,
	events,
	fire,
	fireAsync,
	unsubscribe
}
