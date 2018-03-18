import React from 'react'
import FormButton from '../bootstrap/FormButton.js'
import FormInput from '../bootstrap/FormInput.js'
import FormTextarea from '../bootstrap/FormTextarea.js'
import CloseIcon from '../bootstrap/CloseIcon.js'

class EditConfig extends React.Component {
	constructor() {
		super();
		this.init(true);
	}
	
	init(construct) {
		this.keyValues = [];
		this.commentText = '';
		if (construct) {
			this.state = {saveDisabled: true, doneDisabled: true, key: '', value: '', editedCount: 0};
		}
		else {
			this.setState({saveDisabled: true, doneDisabled: true, key: '', value: '', editedCount: 0});
		}
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['env-selected', 'editing-config'];
	}
	
	envSelected(env) {
		this.selectedEnv = env;
	}
	
	editingConfig(key, value, cancelFn) {
		if (this.editingConfigValue) {
			this.editingConfigValue.cancelFn();
		}
		this.editingConfigValue = {
			key: key,
			value: value,
			cancelFn: cancelFn
		};
		this.setState({key: key, value: value});
	}
	
	done() {
		this.keyValues.push({
			key: this.state.key,
			value: this.state.value,
			cancelFn: this.editingConfigValue.cancelFn
		});
		this.editingConfigValue = undefined;
		this.setState({key: '', value: '', doneDisabled: true, editedCount: (this.state.editedCount + 1)});
	}
	
	cancel() {
		this.editingConfigValue.cancelFn();
		this.editingConfigValue = undefined;
		this.setState({key: '', value: '', doneDisabled: true});
	}
	
	removeEdited(keyVal) {
		for (let i = 0; i < this.keyValues.length; i++) {
			if (this.keyValues[i].key === keyVal.key) {
				this.keyValues.splice(i, 1);
				break;
			}
		}
		keyVal.cancelFn();
		this.setState({editedCount: (this.state.editedCount - 1)});
	}
	
	save() {
		let saveConfig = {
			comment: this.commentText,
			keyVals: {}
		};
		for (let keyVal of this.keyValues) {
			saveConfig.keyVals[keyVal.key] = keyVal.value;
		}
		window.$E.events.loadingStarted.fire(`Saving configuration changes`);
		window.$R.saveConfigurationsUsingPOST({env: this.selectedEnv, body: saveConfig})
					.then((res) => {
						if (res === true) {
							window.$E.events.alertSuccess.fire(` Saved configuration changes`);
							for (let keyVal of this.keyValues) {
								keyVal.cancelFn(keyVal.value);
							}
							this.init();
						}
						else {
							window.$E.events.alertDanger.fire(` unable to save configuration changes`);
						}
					})
					.catch((err) => {
						let errMessage = err.message || '';
						window.$E.events.alertDanger.fire(errMessage);
					})
					.then(() => {
						window.$E.events.loadingFinished.fire();
					});
	}
	
	comment(text) {
		this.commentText = text;
		if (text !== '') {
			this.setState({saveDisabled: false});
		}
	}
	
	valueChange(value) {
		this.setState({value: value, doneDisabled: (this.editingConfigValue.value === value)});
	}
	
	render() {
		let editViewClass = '';
		if (this.state.key === '') {
			editViewClass = 'd-none';
		}
		let readyChanges = [];
		let commentAndSaveClass = '';
		if (this.keyValues.length === 0) {
			commentAndSaveClass = 'd-none';
		}
		else {
			for (let keyVal of this.keyValues) {
				readyChanges.push(<div key={keyVal.key} className="border p-2 mb-1">{`${keyVal.key} = ${keyVal.value}`}<CloseIcon onClick={() => this.removeEdited(keyVal)} /></div>);
			}
		}
		
		return <div className="container">
					<div className="row mb-3">
						<div className="col-md-12">
							{readyChanges}
						</div>
					</div>
					<div className={`row form-row mb-3 ${commentAndSaveClass}`}>
						<div className="col-md-10">
							<FormInput placeholder="Add comment" onChange={this.comment.bind(this)} />
						</div>
						<div className="col-md-2">
							<FormButton text="Save" onClick={this.save.bind(this)} disabled={this.state.saveDisabled} />
						</div>
					</div>
					<div className={`row mb-3 ${editViewClass}`}>
						<div className="col-md-12">
							<FormTextarea label={`Editing ${this.state.key}`} value={this.state.value} onChange={this.valueChange.bind(this)} />
							<div className="form-inline">
								<FormButton text="Done" onClick={this.done.bind(this)} disabled={this.state.doneDisabled} />
								<FormButton text="Cancel" onClick={this.cancel.bind(this)} />
							</div>
						</div>
					</div>
				</div>;
	}
}

export default EditConfig