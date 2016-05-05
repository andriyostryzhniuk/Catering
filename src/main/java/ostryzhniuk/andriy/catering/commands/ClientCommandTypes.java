package ostryzhniuk.andriy.catering.commands;

public enum ClientCommandTypes {

    //for order view
    SELECT_ORDER(),
    SELECT_CLIENT_NAMES(),
    INSERT_ORDER(),
    SELECT_CLIENT_ID(),
    UPDATE_ORDER(),
    DELETE_ORDER(),
    SELECT_ORDERING(),
    UPDATE_ORDERING(),
    DELETE_ORDERING(),
    INSERT_ORDERING(),
    //for client view
    SELECT_CLIENT(),
    INSERT_CLIENT(),
    UPDATE_CLIENT(),
    DELETE_CLIENT(),
    //for menu view
    SELECT_ALL_OF_MENU(),
    SELECT_SOME_TYPE_OF_MENU(),
    SELECT_DISHES_TYPE_NAME(),
    SELECT_DISHES_TYPE_ID(),
    INSERT_MENU(),
    UPDATE_MENU(),
    DELETE_MENU(),
    SELECT_OF_LIKE_NAMES_MENU(),
    //for dishes type
    SELECT_DISHES_TYPE(),
    INSERT_DISHES_TYPE(),
    UPDATE_DISHES_TYPE(),
    DELETE_DISHES_TYPE();

}
