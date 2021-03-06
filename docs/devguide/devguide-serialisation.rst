=============
Serialisation
=============

A Krail application would originally have only required to support
serialisation in a high availability (HA) environment. In general, using
sticky sessions in a clustered environment would be sufficient.

In addition to HA, running a Krail application on Vert.x places a
further requirement for serialisation support. This introduces all the
usual Java serialisation issues of non-Serializable classes.

Specifically, Vaadin is designed in such a way that it holds the entire
UI state to memory, and therefore needs to serialise it to session when
HA or other circumstances need to move a session.

Scope of impact
===============

Given that the entire UI state needs to be serialised, this includes
anything which implements the ``UI`` or ``KrailView``, plus, of course,
anything which they in turn require.

Even though components themselves should be ``Serializable``, the
largest impact is on views. The emphasis in these notes is on views, but
applies equally to ``UI`` implementations.

The design pattern for Krail has been to use Guice constructor injection
of many and varied dependencies into implementations of ``KrailView``.
More of these dependencies could undoubtedly implement ``Serializable``,
but there will always be cases where this is not possible.

The use of Guice introduces a further challenge - in order to be certain
that dependencies are resolved consistently, Guice should be used to
re-construct any transient dependencies after deserialisation.

Objectives
==========

1. Use Guice to re-construct any transient dependencies after
   deserialisation

2. Must allow for transients which are either reconstructed by Guice or
   by developer code (a @NotGuice annotation?)

3. Make the process as simple and clear as possible for Krail
   application developers

4. If possible, make transition to a non-native serialisation process an
   easy option.

5. Allow Krail developers to populate other transient fields as they
   need to, before and/or after the injections

Options and Obstacles
=====================

In order to meet objective 1), any potential resolution requires that
the Guice Injector is available during deserialisation.

Use of Injector.injectMembers
-----------------------------

This would be very easy to implement in a readObject - a simply call
(hooks for the developer to use before and after the injections have
been ignored for this example):

.. code:: java

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        getInjector().injectMembers(this)
    }

Obstacles
~~~~~~~~~

-  This will only work with field or method injection - any
   non-serialisable fields would have to be field or method injected.
   While this would work, it imposes a restriction on constructor
   injection.

-  This would mean abandoning or modifying constructor injection for all
   the current ``KrailView`` implementations

-  Field injection has its own limitations, and most see it as a less
   attractive option. Difficulty of testing is usually overstated
   especially with the introduction of `Bound
   Fields <https://github.com/google/guice/wiki/BoundFields>`__, and the
   choices are discussed in the `Guice
   documentation <https://github.com/google/guice/wiki/Injections>`__.

-  Even if Field injection were considered a good option, it would
   remove the choice from a Krail application developer.

Proxy serialisation
-------------------

An `old
post <https://groups.google.com/forum/#!topic/google-guice/T9VMiv6pgLw>`__
the author made a long time ago, may also provide an answer. The
relevant part is copied below:

    If you can hook the objects that you want serialized by adding a
    writeReplace() method, then you can use a serializable proxy to ship
    each over the wire. The proxy would contain the Key corresponding to
    the binding of the object. The trick here is establishing the
    reverse mapping from { object => key }, but it’s possible through
    use of either the provision API or plain java code (I’ve done the
    latter myself).

    The proxy would have a readResolve() method that can find a handle
    to the injector on the JVM (several options exist for how to do
    this), and then it would return "injector.getInstance(key)." This
    solution would allow you to have non-serializable types as "private
    final transient" instance vars on a serializable object. Looks
    strange, but it’s possible.

    As another solution, if you could only hook the deserialization
    code, you could then declare the non-serializable types as non-final
    and use member injection when types are deserialized (via
    "injector.injectMembers(deserializedObject)").

Obstacles
~~~~~~~~~

-  ``Key`` is not serialisable, and nor is TypeLiteral, which would be
   an alternative. `Guava
   TypeToken <https://github.com/google/guava/wiki/ReflectionExplained>`__
   may be an option

-  Defining a proxy would need to cater for generics, which is
   complicated by ``Key`` not being serialisable

-  Reflection is still required

-  Fields would have to be annotated with binding annotations where
   there are two instances of the same type being injected - there is no
   other way to match a constructor parameter to the field it is
   assigned to.

-  If we can serialise a representation of the key on write, we could
   just as easily construct the key on read, and just inject an instance
   from that Key

Bespoke transient field initialiser
-----------------------------------

It would seem possible to create a routine to populate transient fields
by reflection, using an Injector, as part of readObject, in ``ViewBase``
This might mean repeating the binding annotations on fields (but without
the @Inject), where there are multiple injections of the same type.

Obstacles
~~~~~~~~~

-  Fields would have to be annotated with binding annotations where
   there are two instances of the same type being injected - there is no
   other way to match a constructor parameter to the field it is
   assigned to.

Conclusion
==========

Of these choices, the *Bespoke transient field initialiser* seems to
offer the best solution, in that it all it requires is that the Krail
developer annotates transient fields with binding annotations where
needed.
