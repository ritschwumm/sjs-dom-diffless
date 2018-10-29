package sjs.diffless

import scala.scalajs.js
import org.scalajs.dom.raw._

object syntax extends syntax

trait syntax {
	implicit class TagExt[N<:Node](peer:Tag[N]) {
		def apply[M,A,H](children:Child[N,M,A,H]*):View[M,A,H]	=
				View elementFromChildren (peer, children.toVector)
	}

	implicit class AttributeKeyAttributeExt[K](peer:K) {
		def ~=[N,M,MM](func:M=>MM)(implicit ev:AttributeSetter[K,N,MM]):Attribute[N,M]	= Attribute dynamic ev.proc adaptModel func
		def :=[N,M,MM](value:MM)(implicit ev:AttributeSetter[K,N,MM]):Attribute[N,M]	= Attribute static  (ev.proc, value)

		/*
		sadly, this leads to ambiguous implicits
		def apply[N,M,A](implicit ev:AttributeSetter[K,N,M]):Attribute[N,M]	= Attribute dynamic ev.proc
		*/
	}

	implicit class AttributeKeyEmitExt[K](peer:K) {
		def |=[N,A,E<:Event](handler:(N,E)=>A)(implicit ev:AttributeSetter[K,N,js.Function1[E,_]]):Emit[N,A]	= {
			val attach:(N,E=>Unit)=>Unit	= (n,ae) => ev.proc(n, ae)
			Emit action (attach, handler)
		}
	}
}
