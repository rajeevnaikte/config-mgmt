import React from 'react'

class FormButton extends React.Component {
	render() {
		let type = this.props.type || 'button';
		let onClick = this.props.onClick || '';
		let state = this.props.state || 'info';
		let disabled = this.props.disabled || false;
		
		return <div className="form-group mr-2">
					<button className={`btn btn-default btn-${state}`} type={type} onClick={onClick} disabled={disabled}>{this.props.text}</button>
				</div>;
	}
}

export default FormButton