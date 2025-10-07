package org.vl4ds4m.banking.accounts.repository.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = CustomerPe.TABLE_NAME)
@Data
@NoArgsConstructor
public class CustomerPe {

    static final String TABLE_NAME = "customers";

    @Id
    @GeneratedValue
    @Column(name = ColumnName.ID)
    private Long id;

    @Column(name = ColumnName.NAME,
            nullable = false,
            unique = true)
    private String name;

    @Column(name = ColumnName.FIRST_NAME,
            nullable = false)
    private String firstName;

    @Column(name = ColumnName.LAST_NAME,
            nullable = false)
    private String lastName;

    @Column(name = ColumnName.BIRTH_DATE,
            nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = Account_.CUSTOMER)
    protected Set<AccountPe> accounts;

    static class ColumnName {

        static final String ID = "id";

        static final String NAME = "name";

        static final String FIRST_NAME = "first_name";

        static final String LAST_NAME = "last_name";

        static final String BIRTH_DATE = "birth_date";
    }
}
