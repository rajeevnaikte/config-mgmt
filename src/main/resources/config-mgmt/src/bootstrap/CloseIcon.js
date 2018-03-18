import React from 'react'

class CloseIcon extends React.Component {
	render() {
		return <i className="fa fa-times float-right" onClick={this.props.onClick || ''}></i>;
	}
}

export default CloseIcon