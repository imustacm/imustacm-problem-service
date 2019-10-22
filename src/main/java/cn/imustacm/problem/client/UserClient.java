package cn.imustacm.problem.client;

import cn.imustacm.common.consts.ServiceIdConst;
import cn.imustacm.user.service.IUsersService;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(ServiceIdConst.IMUSTACM_USER_SERVICE)
public interface UserClient extends IUsersService {

}
