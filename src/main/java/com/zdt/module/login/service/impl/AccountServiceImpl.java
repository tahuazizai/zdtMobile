package com.zdt.module.login.service.impl;

import com.zdt.module.login.entity.Account;
import com.zdt.module.login.mapper.AccountDao;
import com.zdt.module.login.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
* <p>
 * 账户表  服务实现类
 * </p>
*
* @author xxxx
* @since 2021-06-17
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {

}
