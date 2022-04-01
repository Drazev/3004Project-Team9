package com.team9.questgame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QuestgameApplicationTests {

	@Autowired
	QuestGameApplication questgameApplication;

	@Test
	void contextLoads() {
		assertThat(questgameApplication).isNotNull();
	}

}
