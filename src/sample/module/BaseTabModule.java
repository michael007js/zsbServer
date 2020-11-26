package sample.module;

import com.sun.istack.internal.NotNull;
import sample.MainController;

public abstract class BaseTabModule {

    public abstract void initialize(@NotNull MainController mainController);
}
