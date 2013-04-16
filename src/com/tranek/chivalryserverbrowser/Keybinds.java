package com.tranek.chivalryserverbrowser;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * A data structure class containing the names of all of the gamepad buttons
 * and the names of all of the commands as used by the UDKSystemSettings.ini.
 *
 */
public class Keybinds {

	//Xbox 360 Controller Buttons
	public static final String XBOXTYPES_START = "XboxTypeS_Start";
	public static final String XBOXTYPES_LEFTX = "XboxTypeS_LeftX";
	public static final String XBOXTYPES_LEFTY = "XboxTypeS_LeftY";
	public static final String XBOXTYPES_RIGHTX = "XboxTypeS_RightX";
	public static final String XBOXTYPES_RIGHTY = "XboxTypeS_RightY";
	public static final String XBOXTYPES_Y = "XboxTypeS_Y";
	public static final String XBOXTYPES_RIGHT_TRIGGER = "XboxTypeS_RightTrigger";
	public static final String XBOXTYPES_LEFT_TRIGGER = "XboxTypeS_LeftTrigger";
	public static final String XBOXTYPES_RIGHT_SHOULDER = "XboxTypeS_RightShoulder";
	public static final String XBOXTYPES_LEFT_SHOULDER = "XboxTypeS_LeftShoulder";
	public static final String XBOXTYPES_RIGHT_THUMBSTICK = "XboxTypeS_RightThumbStick";
	public static final String XBOXTYPES_B = "XboxTypeS_B";
	public static final String XBOXTYPES_X = "XboxTypeS_X";
	public static final String XBOXTYPES_BACK = "XboxTypeS_Back";
	public static final String XBOXTYPES_A = "XboxTypeS_A";
	public static final String XBOXTYPES_LEFT_THUMBSTICK = "XboxTypeS_LeftThumbStick";
	public static final String XBOXTYPES_DPAD_UP = "XboxTypeS_DPad_Up";
	public static final String XBOXTYPES_DPAD_DOWN = "XboxTypeS_DPad_Down";
	public static final String XBOXTYPES_DPAD_LEFT = "XboxTypeS_DPad_Left";
	public static final String XBOXTYPES_DPAD_RIGHT = "XboxTypeS_DPad_Right";
	
	//GBA (Gamepad) Commands
	public static final String GBA_SHOW_MENU = "GBA_ShowMenu";
	public static final String GBA_STRAFE_LEFT_GAMEPAD = "GBA_StrafeLeft_Gamepad";
	public static final String GBA_MOVE_FORWARD_GAMEPAD = "GBA_MoveForward_Gamepad";
	public static final String GBA_TURN_LEFT_GAMEPAD = "GBA_TurnLeft_Gamepad";
	public static final String GBA_LOOK_GAMEPAD = "GBA_Look_Gamepad";
	public static final String GBA_NEXT_WEAPON = "GBA_NextWeapon";
	public static final String GBA_PREV_WEAPON = "GBA_PrevWeapon";
	public static final String GBA_FIRE = "GBA_Fire";
	public static final String GBA_AIM = "GBA_Aim";
	public static final String GBA_ALT_FIRE = "GBA_AltFire";
	public static final String GBA_START_STAB = "GBA_StartStab";
	public static final String GBA_SHOVE = "GBA_Shove";
	public static final String GBA_RELOAD = "GBA_Reload";
	public static final String GBA_FEINT = "GBA_Feint";
	public static final String GBA_USE = "GBA_Use";
	public static final String GBA_SHOW_SCORES = "GBA_ShowScores";
	public static final String GBA_SPRINT = "GBA_Sprint";
	public static final String GBA_ZOOM = "GBA_Zoom";
	public static final String GBA_UP_DUCK = "GBA_UpDuck";
	public static final String GBA_DOWN_DUCK = "GBA_DownDuck";
	public static final String GBA_PLAY_BATTLE_CRY = "GBA_PlayBattleCry";
	public static final String GBA_TOGGLE_XHAIR = "GBA_ToggleXHair";
	public static final String GBA_ARROW_CAM = "GBA_ArrowCam";
	public static final String GBA_F10 = "GBA_F10";
	public static final String GBA_FORWARD_SPAWN = "GBA_ForwardSpawn";
	public static final String GBA_REJECT_KING = "GBA_RejectKing";
	public static final String GBA_NEXT_CAMERA_ANGLE = "GBA_NextCameraAngle";
	public static final String GBA_BEHIND_VIEW = "GBA_Behindview";
	public static final String GBA_VOTEYES = "GBA_VoteYes";
	public static final String GBA_VOTENO = "GBA_VoteNo";
	public static final String GBA_AOCDODGE = "GBA_AOCDodge";
	public static final String GBA_MELEE_FEINT = "GBA_MeleeFeint";
	public static final String GBA_MELEE_LEFT = "GBA_MeleeLeft";
	public static final String GBA_ALT_ATTACK_0 = "GBA_AltAttack0";
	public static final String GBA_ALT_ATTACK_1 = "GBA_AltAttack1";
	public static final String GBA_SPECTATOR_PERSPECTIVE = "GBA_SpectatorPerspective";
	public static final String GBA_SPECTATOR_NEXT = "GBA_SpectatorNext";
	public static final String GBA_SPECTATOR_ZOOM_OUT = "GBA_SpectatorZoomOut";
	public static final String GBA_SPECTATOR_ZOOM_IN = "GBA_SpectatorZoomIn";
	public static final String GBA_SPECTATOR_FREECAM = "GBA_SpectatorFreecam";
	public static final String GBA_SPECTATOR_PREVIOUS = "GBA_SpectatorPrevious";
	
	//Commands
	public static final String JUMP = "Jump";
	public static final String DO_PARRY = "DoParry";
	public static final String ON_RELEASE_LOWER_SHIELD = "Onrelease LowerShield";
	
	/**
	 * All of the commands sorted alphabetically. This is synched with the {@link HashMap}
	 * of the command descriptions.
	 */
	@SuppressWarnings("serial")
	public static final ArrayList<String> commands = new ArrayList<String>() {
		{
			//Commands
			add("GBA_Aim");
			add("GBA_MeleeLeft");
			add("GBA_AltAttack0");
			add("GBA_AltAttack1");
			add("GBA_ArrowCam");
			add("GBA_AOCDodge");
			add("GBA_Behindview");
			add("GBA_SpectatorPerspective");
			add("GBA_DownDuck");
			add("GBA_Feint");
			add("GBA_Fire");
			add("Jump");
			add("GBA_Shove");
			add("GBA_Look_Gamepad");
			add("Onrelease LowerShield");
			add("GBA_MeleeFeint");
			add("GBA_MoveForward_Gamepad");
			add("GBA_NextCameraAngle");
			add("GBA_NextWeapon");
			add("GBA_AltFire");
			add("DoParry");
			add("GBA_PrevWeapon");
			add("GBA_RejectKing");
			add("GBA_Reload");
			add("GBA_ShowScores");
			add("GBA_PlayBattleCry");
			add("GBA_ShowMenu");
			add("GBA_ForwardSpawn");
			add("GBA_SpectatorFreecam");
			add("GBA_SpectatorNext");
			add("GBA_SpectatorPrevious");
			add("GBA_SpectatorZoomIn");
			add("GBA_SpectatorZoomOut");
			add("GBA_Sprint");
			add("GBA_StrafeLeft_Gamepad");
			add("GBA_StartStab");
			add("GBA_UpDuck");
			add("GBA_F10");			
			add("GBA_ToggleXHair");
			add("GBA_TurnLeft_Gamepad");
			add("GBA_Use");
			add("GBA_VoteNo");
			add("GBA_VoteYes");
			add("GBA_Zoom");
		}
	};
	
	/**
	 * All of the command descriptions sorted alphabetically. This is synched with the {@link ArrayList}
	 * of the commands.
	 */
	@SuppressWarnings("serial")
	public static final HashMap<String, String> commandDescriptions = new HashMap<String, String>() {
		{
			//Commands
			put("GBA_Aim", "Aim");
			put("GBA_MeleeLeft", "Alternate attack modifier");
			put("GBA_AltAttack0", "Alternate horizontal attack");
			put("GBA_AltAttack1", "Alternate overhead attack");
			put("GBA_ArrowCam", "Arrow cam");
			put("GBA_AOCDodge", "Autododge");
			put("GBA_Behindview", "Behind view");
			put("GBA_SpectatorPerspective", "Change spectator perspective");
			put("GBA_DownDuck", "Crouch");
			put("GBA_Feint", "Feint");
			put("GBA_Fire", "Horizontal attack");
			put("Jump", "Jump!");
			put("GBA_Shove", "Kick");
			put("GBA_Look_Gamepad", "Look up/down");
			put("Onrelease LowerShield", "Lower shield on release of this button");
			put("GBA_MeleeFeint", "Melee feint");
			put("GBA_MoveForward_Gamepad", "Move forward/backward");
			put("GBA_NextCameraAngle", "Next camera angle");
			put("GBA_NextWeapon", "Next weapon");
			put("GBA_AltFire", "Overhead attack");
			put("DoParry", "Parry/Raise shield");
			put("GBA_PrevWeapon", "Previous weapon");
			put("GBA_RejectKing", "Reject kingship");
			put("GBA_Reload", "Reload");
			put("GBA_ShowScores", "Scoreboard");
			put("GBA_PlayBattleCry", "Scream!");
			put("GBA_ShowMenu", "Show menu");
			put("GBA_ForwardSpawn", "Spawn forward at the start of next objective");
			put("GBA_SpectatorFreecam", "Spectator free camera");
			put("GBA_SpectatorNext", "Spectate next player");
			put("GBA_SpectatorPrevious", "Spectate previous player");
			put("GBA_SpectatorZoomIn", "Spectate zoom in");
			put("GBA_SpectatorZoomOut", "Spectate zoom out");
			put("GBA_Sprint", "Sprint");
			put("GBA_StrafeLeft_Gamepad", "Strafe");
			put("GBA_StartStab", "Stab");
			put("GBA_UpDuck", "Stand up from crouching");
			put("GBA_F10", "Suicide");			
			put("GBA_ToggleXHair", "Toggle crosshair");
			put("GBA_TurnLeft_Gamepad", "Turn left/right");
			put("GBA_Use", "Use");
			put("GBA_VoteNo", "Vote no");
			put("GBA_VoteYes", "Vote yes");
			put("GBA_Zoom", "Zoom");
		}
	};
	
}
