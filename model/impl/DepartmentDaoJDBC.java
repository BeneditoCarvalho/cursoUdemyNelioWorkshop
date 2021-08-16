package model.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

    Connection connection;

    public DepartmentDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Department department) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("insert into department (Name) values (?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, department.getName());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    department.setId(id);
                }
                DB.closeResultSet(resultSet);
            } else {
                throw new DbException("Unexpected error!");
            }
            DB.closeStatement(preparedStatement);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }


    }

    @Override
    public void update(Department department) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("update department set Name = ? where Id = ?");
            preparedStatement.setString(1, department.getName());
            preparedStatement.setInt(2, department.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update successful");
            }

        } catch (SQLException e) {
            throw new DbException("Data not updated!");
        }
        DB.closeStatement(preparedStatement);
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("delete from department where Id = ?");
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delete successful");
            } else {
                System.out.println("Data not found!");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        DB.closeStatement(preparedStatement);

    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("select * from department where id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Department department = new Department();
                department.setId(resultSet.getInt("Id"));
                department.setName(resultSet.getString("Name"));

                return department;

            } else {
                throw new DbException("Error: Data not found!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Department> findAll() {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("select * from department order by Name");
            resultSet = preparedStatement.executeQuery();

            List<Department> departmentList = new ArrayList<>();

            while (resultSet.next()) {

                int id = resultSet.getInt("Id");
                String name = resultSet.getString("Name");

                departmentList.add(new Department(id, name));
            }
            return departmentList;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }
}
