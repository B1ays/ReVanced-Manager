package ru.Blays.ReVanced.Manager.UI.ComponentCallback


class ComponentCallback <T: Function<*>> (override val action: T): IComponentCallback<T> {

    override var onSuccess: (() -> Unit)? = null

    override var onError: (() -> Unit)? = null

    companion object {
        inline fun <reified T: Function<*>> builder(action: T, scope: IComponentCallback<T>.() -> Unit): IComponentCallback<T> {
            val componentCallback = ComponentCallback(action)
            scope(componentCallback)
            return componentCallback
        }

        inline fun <reified T: Function<*>> builder(scope: IComponentCallback<T>.() -> Unit): IComponentCallback<T> {
            val componentCallback = ComponentCallback({} as T)
            scope(componentCallback)
            return componentCallback
        }

        inline fun <reified T : Function<*>> builder(action: T): IComponentCallback<T> {
            return ComponentCallback(action)
        }
    }
}