package telran.cars;

public interface TestNames {

	String ADD_PERSON = "Adding Person";
	String ADD_CAR = "Adding Car";
	String ADD_MODEL = "Adding Model";
	String UPDATE_PERSON = "Updating Person's Email";
	String DELETE_PERSON = "Deleting Person";
	String DELETE_CAR = "Deleting Car";
	String PURCHASE_NEW_OLD_OWNERS = "Purchasing - There are old and new owners ";
	String PURCHASE_ONLY_NEW_OWNER = "Purchasing - There is only new owner ";
	String PURCHASE_NOT_FOUND = "Purchasing - either car or owner not found";
	String PURCHASE_NO_NEW_OWNER = "Purchasing - There is not  new owner ";
	String PURCHASE_SAME_OWNER = "Purchasing - either the same owner or neither old nor new ";
	String GET_OWNER_CARS = "Getting car's data of a given owner";
	String GET_CAR_OWNER = "Getting owner data of a car";
	String MODEL_NAMES_MOST_SOLD = "Getting all most sold model names ";
	String MODEL_NAMES_MOST_POPULAR = "Getting a given number of of most popular model names";
	String TRADE_DEALS_COUNT_MONTH_MODEL = "Getting number of the trade deals at a given month of a given model name";
	String MODEL_NAMES_MOST_POPULAR_OWNER_AGES = "Getting a given number of most popular model names of the owners having a given age's range";
	String COLOR_MOST_POPULR_MODEL = "Getting a given number of most popular colors of a given model name";
	String ENGINE_POWER_CAPACITY_MIN_AGES = "Getting minimal values of engine power and capcity of cars belonging to owners of a given age's range";

}