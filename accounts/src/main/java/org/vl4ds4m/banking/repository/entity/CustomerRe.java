package org.vl4ds4m.banking.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.vl4ds4m.banking.entity.Customer;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = CustomerRe.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerRe {

    static final String TABLE_NAME = "customers";

    @Id
    @GeneratedValue
    @Column(name = ColumnName.ID)
    @EqualsAndHashCode.Include
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

    @OneToMany(mappedBy = AccountRe_.CUSTOMER)
    private Set<AccountRe> accounts;

    public Customer toEntity() {
        return new Customer(name, firstName, lastName, birthDate);
    }

    static class ColumnName {

        static final String ID = "id";

        static final String NAME = "name";

        static final String FIRST_NAME = "first_name";

        static final String LAST_NAME = "last_name";

        static final String BIRTH_DATE = "birth_date";
    }
}
