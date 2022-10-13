sealed interface Role {
    val type: String
    object Engineer : Role {
        override val type: String
            get() = "engineer"
    }

    object Manager : Role {
        override val type: String
            get() = "manager"
    }
}
