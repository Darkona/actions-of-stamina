<p><img src="https://img.shields.io/badge/-ModLoader:%20Forge-lightgrey" alt="" width="111" height="20" /> <img src="https://img.shields.io/badge/-Minecraft%201.19.2-green" alt="" width="101" height="20" /> <img src="https://shields.io/badge/-More%20versions%20&amp;%20features%20are%20work%20in%20progress-informational" alt="" width="265" height="20" /> <img src="https://shields.io/badge/-Requires%20Feathers%20mod-critical" alt="" width="137" height="20" />&nbsp;</p>
<h5><strong>At first glance limitations seem to be disadvantageous. </strong></h5>
<h5><strong>But on closer inspection they just force us to adapt, breaking new ground and using more of our creativity in order to reach our goals.</strong></h5>
<p><span style="font-weight: 400;">&nbsp;</span></p>
<h4><b><strong>🌱Credits</strong></b></h4>
<p><b>I have to thank <a href="https://www.curseforge.com/members/muraokun/projects">muraokun</a>&nbsp;for this wonderful idea - moreover&nbsp;their suggestions really shaped the concept of this mod.<br />Furthermore I really appreciate that <a href="https://www.curseforge.com/members/elenaidev/followers">ElenaiDev</a>&nbsp;allowed me to use the feather icon for the mod logo.</b></p>
<p>&nbsp;</p>
<h4><b><strong>💡About</strong></b></h4>
<p><span style="font-weight: 400;">Actions of Stamina (AoS) is a vanilla friendly survival mod that uses the stamina system of the <a href="https://www.curseforge.com/minecraft/mc-mods/feathers/files">Feathers</a> mod for player actions.</span></p>
<p><span style="font-weight: 400;">It adds logic that&nbsp;<strong>prevents a player from performing an action&nbsp;</strong>when there is no more stamina&nbsp;or not enough of it.</span></p>
<p><span style="font-weight: 400;">Every supported action <strong>consumes feathers</strong>&nbsp;and thereforge <strong>lowers the&nbsp;available stamina</strong>.&nbsp;</span></p>
<p><span style="font-weight: 400;">AoS enables you to define the exact amount of time and times after which feathers will be used.</span></p>
<p><span style="font-weight: 400;">Currently there's only a few of actions that supports stamina but I will add more in the future.</span></p>
<p>&nbsp;</p>
<h4 class="LC20lb MBeuO DKV0Md">📜<strong> Actions that&nbsp;requires and uses stamina&nbsp;</strong>(customizable &amp; deactivatable)</h4>
<ul>
<li style="padding-left: 30px;">Running: If a player doesn't have enough stamina, they can only walk and need to wait a bit for the feathers regneration.</li>
<li style="padding-left: 30px;">Jumping: AoS prevent a player from jumping when not having enough stamina - also supports auto-jump.</li>
<li style="padding-left: 30px;">Crawling: When a player finds them in the crawling position, they can only move if they has enough stamina.</li>
</ul>
<p class="LC20lb MBeuO DKV0Md">AoS also supports and will support actions of other mods - look at the "Compatibilities" section</p>
<p>&nbsp;</p>
<h4 style="text-align: left;"><b>📖 <strong>Getting Started</strong></b></h4>
<p style="text-align: left;"><span style="font-weight: 400;">You can enable or disable whether an action should use feathers (stamina) You can even tweak the amount of feathers an action requires beforehand and uses for and after or while being executed.</span></p>
<p style="text-align: left;"><span style="font-weight: 400;">For configuration I recommend <a href="https://www.curseforge.com/minecraft/mc-mods/configured">Configured</a></span><span style="font-weight: 400;">. There are 3 predefined profiles (difficulty levels) that you can choose from</span><span style="font-weight: 400;">:</span></p>
<ul style="text-align: left;">
<li style="padding-left: 30px;">SLUGGISH: Enables a few actions that requires stamina. Moreover, these do not consume so much stamina.</li>
<li style="padding-left: 30px;">EXHAUSTED (default profile): Adds moderate consumption of stamina when doing something.</li>
<li style="padding-left: 30px;">BREATHLESS: All actions requires a bit of stamina and uses a lot of them. Watch out this can be challenging in some situations.</li>
</ul>
<p style="text-align: left;"><span style="font-weight: 400;">You can either edit these profiles&nbsp;<strong>or you can create your own one</strong> by editing the custom profile. </span></p>
<p style="text-align: left;">&nbsp;</p>
<h4 class="LC20lb MBeuO DKV0Md">👮<strong>Measures against cheating</strong></h4>
<p style="text-align: left;">By default sprinting lowers the feathers bar by 1 visible feather after 5 seconds. If you only run 4 seconds to prevent using stamina and start running again, the</p>
<p style="text-align: left;">feathers bar will be reduced by one after one further second of running. This is achieved by caching the time for which a player performs an action.</p>
<p style="text-align: left;">Furthermore you can configure the initial costs of an action in order to make it more challenging when players start, interrupting and resume actions over and over again.</p>
<p style="text-align: left;">&nbsp;</p>
<h4 class="LC20lb MBeuO DKV0Md" style="text-align: left;">🔌<strong>Compatibilities</strong></h4>
<p style="text-align: left;">AoS adds compatbility features while using other mods -&nbsp;all supported mods are optional&nbsp;dependencies so you can choose which one you want to use:</p>
<p class="LC20lb MBeuO DKV0Md" style="text-align: left;">🥷<a href="https://www.curseforge.com/minecraft/mc-mods/goprone">GoProne</a>&nbsp;/ <a href="https://www.curseforge.com/minecraft/mc-mods/personality">Personality</a> - Feathers will also be used if the player is crawling using these mods.</p>
<p class="LC20lb MBeuO DKV0Md" style="text-align: left;">&nbsp;</p>
<p class="LC20lb MBeuO DKV0Md" style="text-align: left;">More to come&nbsp;- stay tuned🔔</p>
<p style="text-align: left;">&nbsp;&nbsp;</p>
<h4 style="text-align: left;"><b>❗<strong>Important Notes</strong></b></h4>
<ul style="text-align: left;">
<li>I recommend to reduce or disable the impact of armor in the feathers-common.toml (armor weight). Otherwise players will not be able to run when wearing diamond or netherite amor.</li>
<li>Time-based actions (like sprinting) are stopping the regeneration of feathers to be able to set higher values for the time of feathers decrease than the regeneration speed of it.</li>
<li>It's necessary to download the <a href="https://www.curseforge.com/minecraft/mc-mods/feathers/files">Feathers</a> mod when you want to use AoS, because it's a required dependency.</li>
</ul>
<p style="text-align: left;">&nbsp;</p>
<h4 style="text-align: left;"><span style="font-weight: 400;">🔨</span><strong><b>ModLoader</b></strong></h4>
<p style="text-align: left;"><span style="font-weight: 400;">AoS is currently only available for Forge - sorry. Maybe this will change in the future.</span></p>
<p style="text-align: left;">&nbsp;</p>
<h4 style="text-align: left;"><b>📝<strong>FAQ</strong></b></h4>
<div class="spoiler" style="text-align: left;">
<p><strong><b>Q: Can I use AoS in my modpack?</b></strong></p>
<p><span style="font-weight: 400;">A: Yes -&nbsp; Feel free to include AoS into your modpack - Remember to give credit and don't claim AoS as your own creation.</span></p>
<p>&nbsp;</p>
<p><strong><b>Q: Which Minecraft versions are supported?</b></strong></p>
<p><span style="font-weight: 400;">A: AoS is currently available for </span><b>1.19.2.</b></p>
<p>&nbsp;</p>
<p><strong>Q: Are there any known incompatibilities with other mods?</strong></p>
<p>A: No and this shouldn't be the case. If so please report them on GitHub.</p>
<p>&nbsp;</p>
<p><strong><b>Q: Can you add this feature&nbsp; ...?</b></strong></p>
<p><span style="font-weight: 400;">A: Sure - if it matches the concept of AoS. Feel free to post a comment.</span></p>
</div>
<p style="text-align: left;">&nbsp;</p>
<h4 style="text-align: left;"><b>🌎 <strong>Links</strong></b></h4>
<p style="text-align: left;"><a href="https://github.com/CCr4ft3r/actions-of-stamina/issues"><b>Report issues and request features</b></a></p>
<p style="text-align: left;">&nbsp;</p>
<h4 class="LC20lb MBeuO DKV0Md"><strong>🏃Do not run too fast and too long</strong></h4>