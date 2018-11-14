package com.faforever.gw.model;

import com.yahoo.elide.annotation.Include;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Entity
@Include(rootLevel = true)
@Table(name = "gw_item")
public class PassiveItem implements Serializable {
	private UUID id;
	private String name;

	public PassiveItem(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	public UUID getId() {
		return id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
}
