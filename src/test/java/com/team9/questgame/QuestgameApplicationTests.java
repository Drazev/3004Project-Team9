package com.team9.questgame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class QuestgameApplicationTests {

	@Autowired
	QuestgameApplication questgameApplication;

	@Test
	void contextLoads() {
		assertThat(questgameApplication).isNotNull();
	}

}
