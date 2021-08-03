package service;

import dao.FunDao;
import domain.Fun;
import exception.FnameException;
import orm.SqlSession;

import java.util.List;

public class FunService {
    FunDao dao=new SqlSession().getMapper(FunDao.class);
    public List<Fun> findAll(){
        return dao.findAll();
    }

    public void save(Fun fun){
        int count=dao.fnameAssert(fun.getFname());
        if(count>0){
            //名字已经存在
            throw new FnameException(fun.getFname());
        }
        dao.save(fun);
    }

}
