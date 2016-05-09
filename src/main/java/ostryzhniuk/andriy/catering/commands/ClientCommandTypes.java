package ostryzhniuk.andriy.catering.commands;

public enum ClientCommandTypes {

    //for order view
    SELECT_ORDER(),
    SELECT_ORDER_BY_DATE(),
    SELECT_ORDER_BY_ID(),
    SELECT_CLIENT_NAMES(),
    INSERT_ORDER(),
    SELECT_CLIENT_ID(),
    UPDATE_ORDER(),
    DELETE_ORDER(),
    //for ordering view
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
    DELETE_DISHES_TYPE(),
    //for debtors view
    SELECT_DEBTORS(),
    //for order report
    SELECT_ORDER_REPORT(),
    SELECT_MEALS_A_DAY();

}
