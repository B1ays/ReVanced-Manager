package ru.Blays.ReVanced.Manager.UI.ComponentCallback



interface IComponentCallback<T: Function<*>> {

    val action: T

    var onSuccess: (() -> Unit)?

    var onError: (() -> Unit)?
}