import React from 'react';

export function CodeStyleReport(props) {
    return <section className="report">
        {
            props.grade
                ? <section className={"code-style-grade code-style-grade-" + props.grade}/>
                : <section><i className="material-icons">clear</i></section>
        }
    </section>
}