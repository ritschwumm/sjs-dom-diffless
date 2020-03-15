package sjs.diffless

import org.scalajs.dom.raw._

object Emit {
	def emitBuilder[N,M,A,E<:Event](attach:(N,E=>Unit)=>Unit):EmitBuilder[N,E]	= new EmitBuilder[N,E](attach)
	final class EmitBuilder[N,E<:Event](attach:(N,E=>Unit) => Unit) {
		def |=[A](handler:(N,E)=>A):Emit[N,A]	= action(attach, handler)
	}

	def action[N,A,E<:Event](attach:(N,E=>Unit) => Unit, handler:(N,E) => A):Emit[N,A]	=
		Emit { (target, dispatch) =>
			attach(
				target,
				(ev:E) => {
					val action		= handler(target, ev)
					val eventFlow	= dispatch(action)
					applyEventFlow(ev, eventFlow)
				}
			)
		}

	def applyEventFlow(ev:Event, flow:EventFlow):Unit	= {
		flow.defaultAction match {
			case EventDefaultAction.Permit		=>
			case EventDefaultAction.Prevent		=> ev.preventDefault()
		}
		flow.propagation match {
			case EventPropagation.Propagate		=>
			case EventPropagation.Stop			=> ev.stopPropagation()
			case EventPropagation.StopImmediate	=> ev.stopImmediatePropagation()
		}
	}
}

/** tells an element to dispatch certain events */
final case class Emit[-N,+A](setup:(N,A=>EventFlow) => Unit) extends Child[N,Any,A,Nothing] {
	// TODO do we need this?
	def mapAction[AA](func:A=>AA):Emit[N,AA]	=
		Emit { (target, dispatch) =>
			setup(target, func andThen dispatch)
		}
}
