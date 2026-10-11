package queuesmart.Logic;

import queuesmart.Database.ServiceDAO;
import queuesmart.Models.Service;

import java.util.List;

public class ServiceLogic {

    private final ServiceDAO serviceDAO;

    public ServiceLogic() {
        serviceDAO = new ServiceDAO();
    }

    public List<Service> getActiveServices() {
        return serviceDAO.getActiveServices();
    }
}
