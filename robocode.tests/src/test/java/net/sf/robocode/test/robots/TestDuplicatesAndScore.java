/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotTestBed;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestDuplicatesAndScore extends RobotTestBed {
	private IRobotSnapshot[] robots;
	private BattleResults[] results;
	private int lastTurn;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 8;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,tested.robots.TestTeam,tested.robots.TestTeam,sample.Crazy";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			robots = event.getStartSnapshot().getRobots();
			Assert.assertEquals(8, robots.length);

			net.sf.robocode.test.helpers.Assert.assertNear(566.2968069, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(436.3146436, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(717.6994092, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(145.5531935, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.7732607, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(390.8076498, robots[7].getX());

			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[0].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(200.000, robots[1].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(120.000, robots[2].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[3].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(200.000, robots[4].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(120.000, robots[5].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[6].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[7].getEnergy());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();
		robots = event.getTurnSnapshot().getRobots();

		if (lastTurn == 1) {
			net.sf.robocode.test.helpers.Assert.assertNear(566.2968069, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(437.1810299, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(717.6994092, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(145.2637220, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.7732607, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(389.8100070, robots[7].getX());
		}

		if (lastTurn == 1365) {
			net.sf.robocode.test.helpers.Assert.assertNear(456.4088503, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(287.9755313, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(671.5041467, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(258.6924459, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(434.8628269, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(341.4333486, robots[7].getX());
		}
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		if (event.isAborted()) {
			results = null;
		}
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		Assert.assertNotNull(results);
		Assert.assertNotNull(robots);
		Assert.assertThat(robots[0].getName(), is("sample.Fire (1)"));
		Assert.assertThat(robots[1].getName(), is("sampleteam.MyFirstLeader (1)"));
		Assert.assertThat(robots[2].getName(), is("sampleteam.MyFirstDroid (1)"));
		Assert.assertThat(robots[3].getName(), is("sample.Fire (2)"));
		Assert.assertThat(robots[4].getName(), is("sampleteam.MyFirstLeader (2)"));
		Assert.assertThat(robots[5].getName(), is("sampleteam.MyFirstDroid (2)"));
		Assert.assertThat(robots[6].getName(), is("sample.Fire (3)"));
		Assert.assertThat(robots[7].getName(), is("sample.Crazy"));

		Assert.assertThat(results[0].getTeamLeaderName(), is("tested.robots.TestTeam (2)"));
		Assert.assertThat(results[1].getTeamLeaderName(), is("tested.robots.TestTeam (1)"));
		Assert.assertThat(results[2].getTeamLeaderName(), is("sample.Crazy"));
		Assert.assertThat(results[3].getTeamLeaderName(), is("sample.Fire (1)"));

		Assert.assertThat(results[0].getLastSurvivorBonus(), is(50));
		Assert.assertThat(results[1].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[2].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[3].getLastSurvivorBonus(), is(0));

		Assert.assertThat(results[0].getRamDamage(), is(2));
		Assert.assertThat(results[1].getRamDamage(), is(6));
		Assert.assertThat(results[2].getRamDamage(), is(14));
		Assert.assertThat(results[3].getRamDamage(), is(0));

		Assert.assertThat(results[0].getBulletDamageBonus(), is(105));
		Assert.assertThat(results[1].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[2].getBulletDamageBonus(), is(8));
		Assert.assertThat(results[3].getBulletDamageBonus(), is(4));

		Assert.assertThat(results[0].getBulletDamage(), is(523));
		Assert.assertThat(results[1].getBulletDamage(), is(392));
		Assert.assertThat(results[2].getBulletDamage(), is(132));
		Assert.assertThat(results[3].getBulletDamage(), is(99));

		Assert.assertThat(results[0].getScore(), is(1180));
		Assert.assertThat(results[1].getScore(), is(598));
		Assert.assertThat(results[2].getScore(), is(454));
		Assert.assertThat(results[3].getScore(), is(203));

		Assert.assertThat(lastTurn, is(1503));
	}
}
