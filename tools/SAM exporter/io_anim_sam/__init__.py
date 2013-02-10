import bpy;
from bpy_extras.io_utils import (ExportHelper,
                                 axis_conversion,
                                 );
from bpy.props import (FloatProperty,
                       EnumProperty,
                       IntProperty,
                       );
from mathutils import Matrix;

bl_info = {
    "name":         "Spout Animation Model",
    "author":       "Karang, Alkaelis",
    "blender":      (2,6,2),
    "version":      (0,0,1),
    "location":     "File > Import-Export",
    "description":  "Export bones animation for spout engine.",
    "category":     "Import-Export"
}

class ExportSAM(bpy.types.Operator, ExportHelper):
    bl_idname       = "export_anim.sam";
    bl_label        = "Spout Animation Model";
    bl_options      = {'PRESET'};
    
    filename_ext    = ".sam";

    startFrame_prop = IntProperty(name="Start frame",
                                  description="Starting frame for the animation",
                                  default=1);
    endFrame_prop = IntProperty(name="End frame",
                                description="Ending frame for the animation",
                                default=250);

    global_scale = FloatProperty(
            name="Scale",
            description="Scale all data",
            min=0.01, max=1000.0,
            soft_min=0.01,
            soft_max=1000.0,
            default=1.0,
            );

    axis_forward = EnumProperty(
            name="Forward",
            items=(('X', "X Forward", ""),
                   ('Y', "Y Forward", ""),
                   ('Z', "Z Forward", ""),
                   ('-X', "-X Forward", ""),
                   ('-Y', "-Y Forward", ""),
                   ('-Z', "-Z Forward", ""),
                   ),
            default='-Z',
            );

    axis_up = EnumProperty(
            name="Up",
            items=(('X', "X Up", ""),
                   ('Y', "Y Up", ""),
                   ('Z', "Z Up", ""),
                   ('-X', "-X Up", ""),
                   ('-Y', "-Y Up", ""),
                   ('-Z', "-Z Up", ""),
                   ),
            default='Y',
            );

    
    def writeAnimation(self, f, scene, startFrame, endFrame, bone, r_matrix):
        indent = "        ";
        frameId = 1;
        for frame in range(startFrame, endFrame+1):
            scene.frame_set(frame);
            m = r_matrix*bone.matrix_channel;
            f.write(indent + str(frameId) + ": ");
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[0][0], m[0][1], m[0][2], m[0][3]));
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[1][0], m[1][1], m[1][2], m[1][3]));
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[2][0], m[2][1], m[2][2], m[2][3]));
            f.write("%.6f, %.6f, %.6f, %.6f  " % (m[3][0], m[3][1], m[3][2], m[3][3]));
            f.write("\n");
            frameId = frameId+1;

    def saveSAM(self, context, path, startFrame, endFrame, rot_matrix):
        f = open(path, "w", encoding="utf8", newline="\n");

        scene = context.scene;
        obj = context.object;
        armature = obj.parent;

        f.write("Skeleton: skeleton://Spout/entities/Spouty/spouty.ske\n");
        f.write("frames: %d\n" % (endFrame - startFrame + 1));
        f.write("delay: %.6f\n" % (1.0 / (scene.render.fps / scene.render.fps_base)));
        f.write("bones_data:\n");
        
        for bone in armature.pose.bones:
            f.write("    " + bone.name + ":\n");
            self.writeAnimation(f, scene, startFrame, endFrame, bone, rot_matrix);
        
        f.close();
        return {'FINISHED'};
    
    def execute(self, context):
        path = self.as_keywords()["filepath"];
        start = self.as_keywords()["startFrame_prop"];
        end = self.as_keywords()["endFrame_prop"];
        global_matrix = Matrix()
        
        global_matrix[0][0] = \
        global_matrix[1][1] = \
        global_matrix[2][2] = self.global_scale

        global_matrix = (global_matrix *
                         axis_conversion(to_forward=self.axis_forward,
                                         to_up=self.axis_up,
                                         ).to_4x4())
        
        return self.saveSAM(context, path, start, end, global_matrix);

def menu_func(self, context):
    self.layout.operator(ExportSAM.bl_idname, text="Spout Animation Model (.sam)");

def register():
    bpy.utils.register_module(__name__);
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_module(__name__);
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()
